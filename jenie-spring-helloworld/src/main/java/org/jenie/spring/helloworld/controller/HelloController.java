package org.jenie.spring.helloworld.controller;

import org.jenie.spring.helloworld.entity.Person;
import org.jenie.spring.helloworld.repository.PersonRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

	private final PersonRepository personRepository;

	public HelloController(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@GetMapping("/{service}/person/{id}")
	public Person greeting(@PathVariable String service, @PathVariable String id) {
		return this.personRepository.findById(service, id);
	}
	//TODO sl4j 어플리케이션 로깅 처리...

}
