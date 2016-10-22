package ru.mail.park.domain;

public class Greeting {
  private final String name;
  private final String greeting;

  public Greeting(String name, String greeting) {
    this.name = name;
    this.greeting = greeting;
  }

  public String getName() {
    return name;
  }

  public String getGreeting() {
    return greeting;
  }

}
