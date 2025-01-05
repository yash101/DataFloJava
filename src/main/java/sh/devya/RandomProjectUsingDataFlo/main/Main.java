package sh.devya.RandomProjectUsingDataFlo.main;

import sh.devya.DataFloJava2.dataflo.IDataFlo;
import sh.devya.DataFloJava2.resolver.IResolver;

public class Main {
  public static void main(String[] args) {
    // The DataFlo instance will need a resolver
    IResolver resolver = null;

    // Create a DataFlo instance
    IDataFlo dataFlo = null;

    // DataFlo will need some dataplanes.
    IDataPlane dp = dataFlo.getDataPlane();

    // 
  }
}
