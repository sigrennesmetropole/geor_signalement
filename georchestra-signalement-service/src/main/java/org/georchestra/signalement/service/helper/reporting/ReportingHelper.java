/**
 * 
 */
package org.georchestra.signalement.service.helper.reporting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.vividsolutions.jts.geom.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dto.Action;
import org.georchestra.signalement.core.dto.Form;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.Point;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Status;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.core.entity.reporting.LineReportingEntity;
import org.georchestra.signalement.core.entity.reporting.PointReportingEntity;
import org.georchestra.signalement.core.entity.reporting.PolygonReportingEntity;
import org.georchestra.signalement.service.exception.DataException;
import org.georchestra.signalement.service.exception.FormDefinitionException;
import org.georchestra.signalement.service.helper.form.FormHelper;
import org.georchestra.signalement.service.helper.workflow.BpmnHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import net.minidev.json.parser.ParseException;

/**
 * @author FNI18300
 *
 */
@Component
public class ReportingHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingHelper.class);

	private static final int SRID = 4326;

	@Autowired
	private BpmnHelper bpmnHelper;

	@Autowired
	private FormHelper formHelper;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Parse une définition de formulaire
	 * 
	 * @param formDefinition
	 * @return
	 * @throws ParseException
	 */
	public Map<String, Object> hydrateData(String datas) throws DataException {
		Map<String, Object> result = null;
		if (StringUtils.isNotEmpty(datas)) {
			ObjectReader objectReader = objectMapper.readerFor(Map.class);
			try {
				return objectReader.readValue(datas);
			} catch (IOException e) {
				throw new DataException("Failed to hydrate:" + datas, e);
			}
		} else {
			result = new HashMap<>();
		}
		return result;
	}

	/**
	 * Serialize une définition de formulaire
	 * 
	 * @param form
	 * @return
	 * @throws IOException
	 */
	public String deshydrateData(Map<String, Object> datas) throws DataException {
		String result = null;
		if (datas != null) {
			ObjectWriter objectWriter = objectMapper.writer();
			try {
				result = objectWriter.writeValueAsString(datas);
			} catch (JsonProcessingException e) {
				throw new DataException("Failed to deshydrate:" + datas, e);
			}
		}
		return result;
	}

	/**
	 * 
	 * @param geographicType
	 * @return
	 */
	public AbstractReportingEntity createReportingEntity(GeographicType geographicType) {
		AbstractReportingEntity result = null;
		switch (geographicType) {
		case POINT:
			result = new PointReportingEntity();
			break;
		case LINE:
			result = new LineReportingEntity();
			break;
		case POLYGON:
			result = new PolygonReportingEntity();
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * @param reportingEntity
	 * @param geographicType
	 * @return vrai si le type géographique change
	 */
	public boolean checkTransmutationNeeded(AbstractReportingEntity reportingEntity, GeographicType geographicType) {
		return geographicType != reportingEntity.getGeographicType();
	}

	/**
	 * @param reportingEntity
	 * @param geographicType
	 * @return vrai si le context change
	 */
	public boolean checkContextChange(AbstractReportingEntity reportingEntity,
			ContextDescriptionEntity contextDescription) {
		return !reportingEntity.getContextDescription().getId().equals(contextDescription.getId());
	}

	/**
	 * @param reportingEntity
	 * @param geographicType
	 * @return un signalement transmuté
	 */
	public AbstractReportingEntity transmuteReportingEntity(AbstractReportingEntity reportingEntity,
			ContextDescriptionEntity contextDescription) {
		AbstractReportingEntity result = reportingEntity;
		if (checkContextChange(reportingEntity, contextDescription)) {
			if (checkTransmutationNeeded(reportingEntity, contextDescription.getGeographicType())) {
				result = createReportingEntity(contextDescription.getGeographicType());
			}
			result.setContextDescription(contextDescription);
			result.setCreationDate(reportingEntity.getCreationDate());
			result.setUpdatedDate(reportingEntity.getUpdatedDate());
			result.setUuid(reportingEntity.getUuid());
			result.setInitiator(reportingEntity.getInitiator());
			result.setDescription(reportingEntity.getDescription());
			result.setStatus(reportingEntity.getStatus());
			result.setDatas(reportingEntity.getDatas());
		}
		return result;
	}

	/**
	 * Construit une instance de signalement
	 * 
	 * @param contextDescription
	 * @param initiator
	 * @return
	 */
	public AbstractReportingEntity createReportingEntity(ContextDescriptionEntity contextDescription,
			String initiator) {
		AbstractReportingEntity result = createReportingEntity(contextDescription.getGeographicType());
		result.setContextDescription(contextDescription);
		result.setCreationDate(new Date());
		result.setUpdatedDate(result.getCreationDate());
		result.setUuid(UUID.randomUUID());
		result.setInitiator(initiator);
		result.setStatus(Status.DRAFT);
		return result;
	}

	/**
	 * Créé une tâche à partir d'un signalement
	 * 
	 * @param reportingDescription
	 * @return
	 */
	public Task createTaskFromReporting(ReportingDescription reportingDescription) {
		Task task = new Task();
		task.setAsset(reportingDescription);
		task.setCreationDate(reportingDescription.getCreationDate());
		task.setUpdatedDate(reportingDescription.getUpdatedDate());
		task.setStatus(reportingDescription.getStatus());
		task.setInitiator(reportingDescription.getInitiator());
		try {
			task.setForm(formHelper.lookupDraftForm(reportingDescription.getContextDescription()));
			fillFormWithData(task.getForm(), reportingDescription);
		} catch (FormDefinitionException e) {
			LOGGER.warn("Failed to set form for draft:" + reportingDescription.getUuid(), e);
		}
		return task;
	}

	public Task createTaskFromWorkflow(org.activiti.engine.task.Task input, ReportingDescription reportingDescription) {
		Task task = new Task();
		task.setAsset(reportingDescription);
		task.setCreationDate(reportingDescription.getCreationDate());
		task.setUpdatedDate(reportingDescription.getUpdatedDate());
		task.setStatus(reportingDescription.getStatus());
		task.setInitiator(reportingDescription.getInitiator());
		List<Action> actions = bpmnHelper.computeTaskActions(input);
		task.setActions(actions);
		task.setAssignee(input.getAssignee());
		task.setId(input.getId());

		try {
			task.setForm(formHelper.lookupForm(input));
			fillFormWithData(task.getForm(), reportingDescription);
		} catch (FormDefinitionException e) {
			LOGGER.warn("Failed to set form for task:" + input.getId(), e);
		}

		return task;
	}

	public void updateLocalization(AbstractReportingEntity reportingEntity, List<Point> localisation) {
		if (!CollectionUtils.isEmpty(localisation)) {
			// get geometry from coordinate XY
			Geometry geometry = convertCoordinateToGeometry(localisation, reportingEntity.getGeographicType());
			geometry.setSRID(SRID);
			// set geomtrie selon le type
			reportingEntity.setGeometry(geometry);

		} else {
			reportingEntity.setGeometry(null);
		}
	}

	private void fillFormWithData(Form form, ReportingDescription reportingDescription) {
		if (form != null && CollectionUtils.isNotEmpty(form.getSections()) && reportingDescription.getDatas() != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> datas = (Map<String, Object>) reportingDescription.getDatas();
			formHelper.fillForm(form, datas);
		}
	}

	/**
	 * Convertir les coordonnées XY en geometrie Point ou Ligne ou Polygone
	 *
	 *
	 * @return
	 */
	public Geometry convertCoordinateToGeometry(List<Point> localisation, GeographicType geographicType) {
		Geometry geometry = null;
		Coordinate[] coordinates;

		switch (geographicType) {
		case POINT:
			coordinates = getCoordinate(localisation, false);
			geometry = new GeometryFactory().createPoint(coordinates[0]);
			break;
		case LINE:
			coordinates = getCoordinate(localisation, false);
			geometry = new GeometryFactory().createLineString(coordinates);
			break;
		case POLYGON:
			coordinates = getCoordinate(localisation, true);
			LinearRing linear = new GeometryFactory().createLinearRing(coordinates);
			geometry = new Polygon(linear, null, new GeometryFactory());
			break;
		default:
			break;
		}
		return geometry;
	}

	public List<Point> convertGeometryToCoordinate(Geometry geometry, GeographicType geographicType) {
		List<Point> result = null;
		if (geometry != null) {
			result = new ArrayList<>();
			Coordinate[] coordinates = geometry.getCoordinates();
			if (ArrayUtils.isNotEmpty(geometry.getCoordinates())) {
				for (int i = 0; i < coordinates.length; i++) {
					if (geographicType == GeographicType.POLYGON && i == coordinates.length - 1) {
						break;
					}
					Coordinate coordinate = coordinates[i];
					Point point = new Point();
					point.setX(Double.toString(coordinate.x));
					point.setY(Double.toString(coordinate.y));
					result.add(point);
				}
			}
		}
		return result;
	}

	/**
	 * récuperer la liste des coordonées à partir d'un tableau de x et de y
	 * 
	 * @param localisation
	 * @param isClosed     (true dans le cas d'un polygone pour ajouter la dernier
	 *                     coordonne qui est la meme que le premiere)
	 * @return
	 */
	public Coordinate[] getCoordinate(List<Point> localisation, boolean isClosed) {
		int size = localisation.size();
		Coordinate[] coordinates;
		if (isClosed == false) {
			coordinates = new Coordinate[size];
			for (int i = 0; i < size; i++) {
				addCoordinate(localisation, coordinates, i);
			}
		} else {
			// on ajoute la derniere coordonnée qui est la meme que la premiere coordonnée
			// pour fermer le polygone
			coordinates = new Coordinate[size + 1];
			for (int i = 0; i < size; i++) {
				addCoordinate(localisation, coordinates, i);
			}
			coordinates[size] = new Coordinate(coordinates[0].x, coordinates[0].y);
		}
		return coordinates;
	}

	private void addCoordinate(List<Point> localisation, Coordinate[] coordinates, int i) {
		Double coordinateX = Double.valueOf(localisation.get(i).getX());
		Double coordinateY = Double.valueOf(localisation.get(i).getY());
		coordinates[i] = new Coordinate(coordinateX, coordinateY);
	}
}
