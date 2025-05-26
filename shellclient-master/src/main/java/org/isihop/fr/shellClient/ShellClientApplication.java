package org.isihop.fr.shellClient;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication
@EnableKafka
@CommandScan
public class ShellClientApplication{

	public static void main(String[] args) {
                SpringApplication application = new SpringApplication(ShellClientApplication.class);
		application.setBannerMode(Mode.OFF);
                application.run(args);
	}

}
