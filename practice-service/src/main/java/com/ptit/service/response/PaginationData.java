package com.ptit.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationData<T> {
    private List<T> data;
    private MetaData metaData;

    // Constructor từ Page với mapping
    public PaginationData(Page<?> page, Class<T> responseClass) {
        ModelMapper mapper = new ModelMapper();
        List<T> list = new ArrayList<>();
        page.getContent().forEach(ob -> {
            list.add(mapper.map(ob, responseClass));
        });
        this.data = list;
        this.metaData = new MetaData(page);
    }

    // Constructor từ Page đã được map
    public PaginationData(Page<T> page) {
        this.data = page.getContent();
        this.metaData = new MetaData(page);
    }

    // Constructor từ Pageable, total và list input
    public PaginationData(Pageable page, Long total, List<?> listInput, Class<T> responseClass) {
        ModelMapper mapper = new ModelMapper();
        List<T> list = new ArrayList<>();
        listInput.forEach(ob -> {
            list.add(mapper.map(ob, responseClass));
        });
        this.data = list;
        this.metaData = new MetaData(total, page);
    }

    // Static factory methods
    public static <T> PaginationData<T> fromPage(Page<T> page) {
        return new PaginationData<>(page.getContent(), new MetaData(page));
    }

    public static <T, S> PaginationData<S> fromPageWithMapping(Page<T> page, Class<S> responseClass) {
        return new PaginationData<>(page, responseClass);
    }

    public static <T> PaginationData<T> fromPageableAndList(Pageable page, Long total, List<T> list) {
        return new PaginationData<>(list, new MetaData(total, page));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MetaData {
        private int page;
        private int size;
        private int totalPage;
        private Long total;

        public <T> MetaData(Page<T> page) {
            this.size = page.getSize();
            this.page = page.getNumber();
            this.total = page.getTotalElements();
            this.totalPage = page.getTotalPages();
        }

        public <T> MetaData(Long total, Pageable page) {
            this.size = page.getPageSize();
            this.page = page.getPageNumber();
            this.total = total;
            this.totalPage = Math.toIntExact(total / page.getPageSize() + 1);
        }
    }
} 