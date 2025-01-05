package sh.devya.RandomProjectUsingDataFlo.service;

import sh.devya.DataFloJava2.dataflo.IDataFlo;
import sh.devya.DataFloJava2.resolver.IResolver;
import sh.devya.DataFloJava2.service.IService;

public class Service {
    public static void main(String[] args) {
    // The DataFlo instance will need a resolver
    IResolver resolver = null;

    // Create a DataFlo instance
    IDataFlo dataFlo = null;

    // DataFlo will need some dataplanes.
    IDataPlane dp = dataFlo.getDataPlane();

    // Create the service object
    IService service = null;
    service.connect(dp);
  }
}
