package ru.mail.park.service;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.mail.park.domain.Greeting;

@Service
@Transactional
public class GreetingService {
  private final JdbcTemplate template;

  public GreetingService(JdbcTemplate template) {
    this.template = template;
  }

  public List<Greeting> list() {
    return template.query("select * from greetings",
        (rs, rowNum) -> new Greeting(rs.getString("name"), rs.getString("greeting")));
  }

  public void create(Greeting greeting) {
    //вообще у нас нет гарантии что у более новой записи будет больший id
    template.update(
        "delete from greetings where id < (select min(id) from (select id from greetings order by id desc limit 100) as desc_ids)");
    
    template.update("insert into greetings(name, greeting) values(?,?)", greeting.getName(),
        greeting.getGreeting());
  }

}
