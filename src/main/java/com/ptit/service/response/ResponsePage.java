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
public class ResponsePage<T, S> {
  private List<S> data;
  private MetaData metaData;

  public ResponsePage(Page<T> page, Class<S> s) {
    ModelMapper mapper = new ModelMapper();
    List<S> list = new ArrayList<>();
    page.getContent().forEach(ob -> {
      list.add(mapper.map(ob, s));
    });
    data = list;
    metaData = new MetaData(page);
  }

  public ResponsePage(Page<S> page) {
    data = page.getContent();
    metaData = new MetaData(page);
  }

  public ResponsePage(Pageable page, Long total, List<T> listInput, Class<S> s) {
    ModelMapper mapper = new ModelMapper();
    List<S> list = new ArrayList<>();
    listInput.forEach(ob -> {
      list.add(mapper.map(ob, s));
    });
    data = list;
    metaData = new MetaData(total, page);
  }
  @Data
  public static class MetaData {
    private int page;
    private int size;
    private int totalPage;
    private Long total;

    public <T> MetaData(Page<T> page) {
      size = page.getSize();
      this.page = page.getNumber();
      this.total = page.getTotalElements();
      this.totalPage = page.getTotalPages();
    }

    public <T> MetaData(Long total, Pageable page) {
      size = page.getPageSize();
      this.page = page.getPageNumber();
      this.total = total;
      this.totalPage = Math.toIntExact(total / page.getPageSize() + 1);
    }

  }
}