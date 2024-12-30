package com.ptit.service.app.responses;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class ResponsePage<T> {
  private List<T> data;
  private Metadata metadata;

  public ResponsePage(Page<T> page) {
    data = page.getContent();
    metadata = new Metadata(page);
  }

  public ResponsePage(Page page, List<T> list) {
    data = list;
    metadata = new Metadata(page);
  }

  @Data
  public static class Metadata {
    private int page = 0;
    private int size = 20;
    private long total = 0;
    private int totalPage = 0;

    public <T> Metadata(Page<T> page) {
      size = page.getSize();
      this.page = page.getNumber();
      this.total = page.getTotalElements();
      this.totalPage = page.getTotalPages();
    }
  }
}
