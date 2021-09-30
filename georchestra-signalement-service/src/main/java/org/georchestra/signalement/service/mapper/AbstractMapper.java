package org.georchestra.signalement.service.mapper;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * @param <E> entity
 * @param <D> DTO
 */
public interface AbstractMapper<E, D> {

    /**
     * @param dto dto to transform to entity
     * @return entity
     */
    E dtoToEntity(D dto);

    /**
     * @param entity entity to transform to dto
     * @return dto
     */
    D entityToDto(E entity);
    
	/**
	 * @param dtos
	 * @return la liste d'entité converties
	 */
	List<E> dtoToEntities(List<D> dtos);

	/**
	 * @param entities
	 * @return la liste de dtos convertis
	 */
	List<D> entitiesToDto(Collection<E> entities);
    
    default Page<D> entitiesToDto(Page<E> entities, Pageable pageable) {
		return new PageImpl<>(entitiesToDto(entities.getContent()), pageable, entities.getTotalElements());
	}

}
