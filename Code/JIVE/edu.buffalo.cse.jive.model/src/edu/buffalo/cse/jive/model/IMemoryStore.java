package edu.buffalo.cse.jive.model;

import java.io.IOException;

public interface IMemoryStore
{
  public byte[] getBytes(IReference ref);

  public String getString(IReference ref);

  public void close() throws IOException;

  public IReference putBytes(byte[] data);

  public IReference putStorable(IStorable storable);

  public IReference putString(String string);

  public long size();

  public interface IReference
  {
  }

  public interface IStorable
  {
    // bytes representing the raw object
    public byte[] bytes();
  }
}
