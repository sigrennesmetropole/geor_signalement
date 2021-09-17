package org.georchestra.signalement.service.acl.impl;

import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.dao.acl.GeographicAreaCustumDao;
import org.georchestra.signalement.core.dao.acl.GeographicAreaDao;
import org.georchestra.signalement.core.dto.GeographicArea;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.georchestra.signalement.service.acl.GeographicAreaService;
import org.georchestra.signalement.service.mapper.acl.GeographicAreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeographicAreaServiceImpl implements GeographicAreaService {

    @Autowired
    private GeographicAreaCustumDao geographicAreaCustumDao;

    @Autowired
    private GeographicAreaMapper geographicAreaMapper;

    @Autowired
    GeographicAreaDao geographicAreaDao;

    @Override
    public List<GeographicArea> searchGeographicAreaIntersections(Geometry geometry, GeographicType geographicType, Long idContext, Long idRole) {

        // On cherche d'abord l'id de la geographic Area
        List<GeographicAreaEntity> geographicAreaEntities = geographicAreaCustumDao.searchGeographicAreaIntersections(geometry, geographicType, idContext, idRole);
        // On convertie les entités en dtos
        return geographicAreaMapper.entitiesToDtos(geographicAreaEntities);
    }

    @Override
    public Page<GeographicArea> searchGeographicAreas(Pageable pageable, String name) {

        GeographicAreaEntity exampleEntity = new GeographicAreaEntity();
        exampleEntity.setNom(name);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase(true)
                .withIgnoreNullValues()
                .withMatcher("nom", ExampleMatcher.GenericPropertyMatchers.contains());

        Example<GeographicAreaEntity> example = Example.of(exampleEntity, matcher);
        return geographicAreaDao.findAll(example, pageable).map(entity -> geographicAreaMapper.entityToDto(entity));
    }

    @Override
    public GeographicArea getGeographicArea(Long id) {
        return geographicAreaMapper.entityToDto(geographicAreaDao.findById(id).orElse(null));
    }
}
