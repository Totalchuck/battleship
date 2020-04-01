package com.codeoftheweb.salvo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import java.lang.reflect.Array;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class);
	}


	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository repositoryGame, GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {


		return (args) -> {
			Game myFirstGame = new Game();
			Game mySecondGame = new Game();

			Player myFirstPlayer = new Player("mike@mail.com");
			Player mySecondPlayer = new Player("stef@mail.com");
			Player myThirdPlayer = new Player("leyla@mail.com");

			GamePlayer myFirstGamePlayer = new GamePlayer();
			GamePlayer mySecondGamePlayer = new GamePlayer();
			GamePlayer myThirdGamePlayer = new GamePlayer();
			GamePlayer myFourthGamePlayer = new GamePlayer();

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


			Ship myFirstCruiser = new Ship("Cruiser", "B5", true);
			Ship myFirstDestroyer = new Ship("Destroyer","G6", false);
			myFirstCruiser.setGamePlayer(myFirstGamePlayer);
			myFirstDestroyer.setGamePlayer(myFirstGamePlayer);

			Ship mySecondCruiser = new Ship("Cruiser", "F2", true);
			Ship mySecondDestroyer = new Ship("Destroyer","D6", false);
			mySecondCruiser.setGamePlayer(myThirdGamePlayer);
			mySecondDestroyer.setGamePlayer(myThirdGamePlayer);

			Salvo myFirstSalvo = new Salvo();
			Salvo mySecondSalvo = new Salvo();

			myFirstSalvo.setGamePlayer(myFirstGamePlayer);
			mySecondSalvo.setGamePlayer(myThirdGamePlayer);

			myFirstSalvo.fireSalvo("B7");
			mySecondSalvo.fireSalvo("G5");
			myFirstSalvo.fireSalvo("F8");
			mySecondSalvo.fireSalvo("D6");


			playerRepository.save(myFirstPlayer);
			playerRepository.save(mySecondPlayer);
			playerRepository.save(myThirdPlayer);
			repositoryGame.save(myFirstGame);
			repositoryGame.save(mySecondGame);
			gamePlayerRepository.save(new GamePlayer());
			gamePlayerRepository.save(myFirstGamePlayer);
			gamePlayerRepository.save(mySecondGamePlayer);
			gamePlayerRepository.save(myThirdGamePlayer);
			gamePlayerRepository.save(myFourthGamePlayer);
			scoreRepository.save(myFirstGamePlayerScore);
			scoreRepository.save(mySecondGamePlayerScore);
			scoreRepository.save(myThirdGamePlayerScore);
			scoreRepository.save(myFourthGamePlayerScore);
			shipRepository.save(myFirstCruiser);
			shipRepository.save(myFirstDestroyer);
			shipRepository.save(mySecondCruiser);
			shipRepository.save(mySecondDestroyer);
			salvoRepository.save(myFirstSalvo);
			salvoRepository.save(mySecondSalvo);





		};

	}

}
