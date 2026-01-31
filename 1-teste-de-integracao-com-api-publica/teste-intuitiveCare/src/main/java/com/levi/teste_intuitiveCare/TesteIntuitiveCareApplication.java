package com.levi.teste_intuitiveCare;

import com.levi.teste_intuitiveCare.service.AnsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TesteIntuitiveCareApplication implements CommandLineRunner {

	private final AnsService ansService;

    public TesteIntuitiveCareApplication(AnsService ansService) {
        this.ansService = ansService;
    }

    public static void main(String[] args) {
		SpringApplication.run(TesteIntuitiveCareApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ansService.executar();
	}
}