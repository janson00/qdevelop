package cn.qdevelop.auth.utils;

public interface IStatusFormatter {
  public void add(String key,Object value);
  public void flush();
  public String toString();
}
