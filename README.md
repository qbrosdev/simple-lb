# simple-lb

This is a simple load balancer that supports **Random** and **Round Robin** strategies.

## Project Structure

The source code is divided into two directories:

- **com.qbros.lb.core**: Files in this directory constitute the **load balancer implementation**.

- **com.qbros.lb.infrastructure**: Two http endpoints are defined in this directory (using ***Spring boot***) to make
  the end-to-end testing of the loadbalancer easier. To call the exposed http endpoints you can use the **Intellij
  Http-Client** files   (random.http and round_robin.http) located in the root directory.
