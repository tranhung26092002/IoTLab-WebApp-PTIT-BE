package com.ptit.service.app.responses;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class ResponsePage<T> {
  private List<T> data;
  private MetaData metaData;

  public ResponsePage(Page<T> page) {
    data = page.getContent();
    metaData = new MetaData(page);
  }

  public ResponsePage(Page page, List<T> list) {
    data = list;
    metaData = new MetaData(page);
  }

  @Data
  public static class MetaData {
    private int page = 0;
    private int size = 20;
    private long total = 0;
    private int totalPage = 0;

    public <T> MetaData(Page<T> page) {
      size = page.getSize();
      this.page = page.getNumber();
      this.total = page.getTotalElements();
      this.totalPage = page.getTotalPages();
    }
  }
}
