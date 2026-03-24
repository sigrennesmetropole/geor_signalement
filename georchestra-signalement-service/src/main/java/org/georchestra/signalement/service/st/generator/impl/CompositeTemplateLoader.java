/**
 * 
 */
package org.georchestra.signalement.service.st.generator.impl;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.georchestra.signalement.service.st.generator.GenerationConnectorConstants;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Slf4j
public class CompositeTemplateLoader implements TemplateLoader {

	private static final String STRING_TEMPLATE_SOURCE = "StringTemplateSource";

	private static final int FILE_TEMPLATE_LOADER_INDEX = 0;

	private static final int CLASS_TEMPLATE_LOADER_INDEX = 1;

	private static final int STRING_TEMPLATE_LOADER_INDEX = 2;

	private List<TemplateLoader> templateLoaders = new ArrayList<>();

	/**
	 * @throws IOException
	 * 
	 */
	public CompositeTemplateLoader(File fileTemplateDirectory, ClassLoader classLoader, String basePackagePath)
			throws IOException {
		if (!fileTemplateDirectory.exists()) {
			fileTemplateDirectory.mkdirs();
		}
		log.info("initialise CompositeTemplateLoader avec le répertoire de template de fichier {} et le package {}",
				fileTemplateDirectory.getAbsolutePath(), basePackagePath);
		templateLoaders.add(new FileTemplateLoader(fileTemplateDirectory));
		templateLoaders.add(new ClassTemplateLoader(classLoader, basePackagePath));
		templateLoaders.add(new StringTemplateLoader());
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		Object result = null;
		log.info("Recherche du template {} dans les loaders", name);
		if (name.startsWith(GenerationConnectorConstants.STRING_TEMPLATE_LOADER_PREFIX)) {
			result = templateLoaders.get(STRING_TEMPLATE_LOADER_INDEX).findTemplateSource(name);
		} else {
			for (TemplateLoader templateLoader : templateLoaders) {
				log.info("Recherche du template {} dans le loader {}", name, templateLoader.getClass().getSimpleName());
				result = templateLoader.findTemplateSource(name);
				if (result != null) {
					log.info("Template {} trouvé dans le loader {}", name, templateLoader.getClass().getSimpleName());
					break;
				}
			}
		}
		return result;
	}

	@Override
	public long getLastModified(Object templateSource) {
		if (templateSource instanceof File) {
			return templateLoaders.get(FILE_TEMPLATE_LOADER_INDEX).getLastModified(templateSource);
		} else if (templateSource.getClass().getSimpleName().endsWith(STRING_TEMPLATE_SOURCE)) {
			return templateLoaders.get(STRING_TEMPLATE_LOADER_INDEX).getLastModified(templateSource);
		} else {
			return templateLoaders.get(CLASS_TEMPLATE_LOADER_INDEX).getLastModified(templateSource);
		}
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		if (templateSource instanceof File) {
			return templateLoaders.get(FILE_TEMPLATE_LOADER_INDEX).getReader(templateSource, encoding);
		} else if (templateSource.getClass().getSimpleName().endsWith(STRING_TEMPLATE_SOURCE)) {
			return templateLoaders.get(STRING_TEMPLATE_LOADER_INDEX).getReader(templateSource, encoding);
		} else {
			return templateLoaders.get(CLASS_TEMPLATE_LOADER_INDEX).getReader(templateSource, encoding);
		}
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		if (templateSource instanceof File) {
			templateLoaders.get(FILE_TEMPLATE_LOADER_INDEX).closeTemplateSource(templateSource);
		} else if (templateSource.getClass().getSimpleName().endsWith(STRING_TEMPLATE_SOURCE)) {
			templateLoaders.get(STRING_TEMPLATE_LOADER_INDEX).closeTemplateSource(templateSource);
		} else {
			templateLoaders.get(CLASS_TEMPLATE_LOADER_INDEX).closeTemplateSource(templateSource);
		}
	}

	public void putTemplate(String name, String templateContent) {
		if (!name.startsWith(GenerationConnectorConstants.STRING_TEMPLATE_LOADER_PREFIX)) {
			throw new IllegalArgumentException(
					"Template name must start with:" + GenerationConnectorConstants.STRING_TEMPLATE_LOADER_PREFIX);
		}
		((StringTemplateLoader) templateLoaders.get(STRING_TEMPLATE_LOADER_INDEX)).putTemplate(name, templateContent);
	}

}
