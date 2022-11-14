package com.example.officefiles.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TextNodesXPath {
  P("paragraph", "//w:p"), //w:p - 1 đoạn văn (khi xuống dòng)
  R("run", "//w:r"), //w:r - 1 câu (có style giống nhau)
  LRPB("lastRenderedPageBreak", "//w:lastRenderedPageBreak");

  private String key;

  private String value;
}
