package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

/*	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository repositoryGame, GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {


		return (args) -> {
			Game myFirstGame = new Game();
			Game mySecondGame = new Game();
			Game myThirdGame = new Game();

			Player myFirstPlayer = new Player("mike@mail.com", "mikepass");
			Player mySecondPlayer = new Player("stef@mail.com", "stefpass");
			Player myThirdPlayer = new Player("leyla@mail.com", "leylapass");

			GamePlayer myFirstGamePlayer = new GamePlayer();
			GamePlayer mySecondGamePlayer = new GamePlayer();
			GamePlayer myThirdGamePlayer = new GamePlayer();
			GamePlayer myFourthGamePlayer = new GamePlayer();
			GamePlayer myFifthGamePlayer = new GamePlayer();

			Score myFirstGamePlayerScore = new Score(1);
			Score mySecondGamePlayerScore = new Score(0.5);
			Score myThirdGamePlayerScore = new Score(0);
			Score myFourthGamePlayerScore = new Score(0.5);

			myFirstGamePlayer.setPlayer(myFirstPlayer);
			myFirstGamePlayer.setGame(myFirstGame);
			myFirstGamePlayerScore.setGame(myFirstGame);
			myFirstGamePlayerScore.setPlayer(myFirstPlayer);

			mySecondGamePlayer.setPlayer(myFirstPlayer);
			mySecondGamePlayer.setGame(mySecondGame);
			mySecondGamePlayerScore.setGame(mySecondGame);
			mySecondGamePlayerScore.setPlayer(myFirstPlayer);

			myThirdGamePlayer.setPlayer(mySecondPlayer);
			myThirdGamePlayer.setGame(myFirstGame);
			myThirdGamePlayerScore.setPlayer(mySecondPlayer);
			myThirdGamePlayerScore.setGame(myFirstGame);

			myFourthGamePlayer.setPlayer(myThirdPlayer);
			myFourthGamePlayer.setGame(mySecondGame);
			myFourthGamePlayerScore.setGame(mySecondGame);
			myFourthGamePlayerScore.setPlayer(myThirdPlayer);

			myFifthGamePlayer.setPlayer(myThirdPlayer);
			myFifthGamePlayer.setGame(myThirdGame);








			playerRepository.save(myFirstPlayer);
			playerRepository.save(mySecondPlayer);
			playerRepository.save(myThirdPlayer);

			repositoryGame.save(myFirstGame);
			repositoryGame.save(mySecondGame);
			repositoryGame.save(myThirdGame);

			gamePlayerRepository.save(myFirstGamePlayer);
			gamePlayerRepository.save(mySecondGamePlayer);
			gamePlayerRepository.save(myThirdGamePlayer);
			gamePlayerRepository.save(myFourthGamePlayer);
			gamePlayerRepository.save(myFifthGamePlayer);

			scoreRepository.save(myFirstGamePlayerScore);
			scoreRepository.save(mySecondGamePlayerScore);
			scoreRepository.save(myThirdGamePlayerScore);
			scoreRepository.save(myFourthGamePlayerScore);




		};

	}

*/
}





@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;


	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(email-> {
			Player player = playerRepository.findByEmail(email);
			if (player != null) {
				return new User(player.getEmail(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + email);
			}
		});
	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.authorizeRequests()
				.antMatchers("/api/games").permitAll()
				.antMatchers("/api/leader_board").permitAll()
				.antMatchers("/web/games.html").permitAll()
				.antMatchers("/web/games.js").permitAll()
				.antMatchers("/web/game.html").permitAll()
				.antMatchers("/web/index.html").permitAll()
				.antMatchers("/web/style/style.css").permitAll()
				.antMatchers("/web/script/games.js").permitAll()
				.antMatchers("/api/players").permitAll()
				.antMatchers("/api/gamePlayer_view").permitAll()
				.antMatchers("/api/game_view/*").hasAnyAuthority("USER")
				.antMatchers("/rest/*").permitAll()
				.antMatchers("/api/login").permitAll()
				.antMatchers("/username").permitAll()
				.anyRequest().fullyAuthenticated()
				.and()
				.formLogin()
				.usernameParameter("email")
				.passwordParameter("password")
				.loginPage("/api/login");
		http.logout().logoutUrl("/api/logout");

		http.csrf().disable();
		http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
		http.formLogin().successHandler((request, response, authentication) -> clearAuthenticationAttribute(request));
		http.formLogin().failureHandler((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttribute(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if(session != null){
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}




