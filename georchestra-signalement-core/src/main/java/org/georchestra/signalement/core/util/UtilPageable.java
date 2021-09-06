package org.georchestra.signalement.core.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Component
public class UtilPageable {

    private final int paginationSize;

    public UtilPageable(@Nullable Integer paginationSize) {
        if (paginationSize == null) {
            this.paginationSize = 10;
        } else {
            this.paginationSize = paginationSize;
        }
    }

    /**
     * @param offset         premier élément de la page
     * @param limit          nombre d'éléments dans la page (= pagination.size)
     * @param sortExpression champs utilisés pour le tri, séparés par des virgules, null si non trié
     */
    public Pageable getPageable(@Nullable Integer offset, @Nullable Integer limit, @Nullable final String sortExpression) {

        if (offset == null || offset < 0) {
            offset = 0;
        }
        if (limit == null || limit < 0) {
            limit = paginationSize;
        }
        if (StringUtils.isNotEmpty(sortExpression)) {
            final List<Sort.Order> orders = getOrders(sortExpression);
            return PageRequest.of(offset, limit, Sort.by(orders));
        } else {
            return PageRequest.of(offset, limit);
        }
    }

    /**
     * @param sortExpression champs utilisés pour le tri, séparés par des virgules, préfixés par un "-" si tri descendant
     * @return tous les tris de champs correspondants
     */
    private List<Sort.Order> getOrders(@Nonnull final String sortExpression) {

        final List<Sort.Order> listOrders = new ArrayList<>();

        // Get param from sortExpression
        final String[] filter = sortExpression.split(",");

        Sort.Order orders;
        for (final String f : filter) {

            if (f.startsWith("-")) {
                orders = new Sort.Order(Sort.Direction.DESC, f.substring(1));
            } else {
                orders = new Sort.Order(Sort.Direction.ASC, f);
            }
            listOrders.add(orders);
        }
        return listOrders;
    }
}
