package com.consoul.movesary.bootstraps;

import java.time.LocalDate;
import java.time.Month;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.consoul.movesary.models.Move;
import com.consoul.movesary.models.User;
import com.consoul.movesary.repositories.MoveRepository;
import com.consoul.movesary.repositories.UserRepository;

@Component
public class DataLoader implements CommandLineRunner{

    private static final String BASIC_JUMP_WHILE_POPPING_THE_NOSE = "basic jump while popping the nose";
	private static final String BASIC_JUMP_WHILE_POPPING_THE_TAIL = "basic jump while popping the tail";
	private static final String OLLIE_VIDEO_URL = "https://www.youtube.com/watch?v=0is-9ltin_A";
	private static final String NOLLIE_VIDEO_URL = "https://www.youtube.com/watch?v=3CCoWOzUBe8";
	private static final String SHUVIT_VIDEO_URL = "https://www.youtube.com/watch?v=_aENJdwK6EM";

	MoveRepository moveRepository;
	UserRepository userRepository;

	public DataLoader(MoveRepository moveRepository, UserRepository userRepository) {
		this.moveRepository = moveRepository;
		this.userRepository = userRepository;
	}

	@Override
	public void run(String... args) {

		int count = moveRepository.getAll().size();

        if (count == 0 ){
            loadData();
        }
	}
	private void loadData() {
		User user1 = new User("userName1", "First User", "user1@gmail.com", LocalDate.now().minusDays(1L)); //yesterday
    	User user2 = new User("userName2", "Second User", "user2@gmail.com", LocalDate.now());
    	User user3 = new User("userName3", "Third User", "user3@gmail.com", nextMonday(LocalDate.now())); // Next Monday

    	userRepository.create(user1);
    	userRepository.create(user2);
    	userRepository.create(user3);

    	Move move1User1 = new Move("Ollie", BASIC_JUMP_WHILE_POPPING_THE_TAIL, OLLIE_VIDEO_URL, LocalDate.of(2020, Month.NOVEMBER, 30), user1);

    	Move move1User2 = new Move("Ollie", BASIC_JUMP_WHILE_POPPING_THE_TAIL, OLLIE_VIDEO_URL, LocalDate.now(), user2);
    	Move move2User2 = new Move("Nollie", BASIC_JUMP_WHILE_POPPING_THE_NOSE, NOLLIE_VIDEO_URL ,LocalDate.now(), user2);

    	Move move1User3 = new Move("Ollie", BASIC_JUMP_WHILE_POPPING_THE_TAIL, OLLIE_VIDEO_URL, LocalDate.of(2020, Month.NOVEMBER, 30), user3);
    	Move move2User3 = new Move("Nollie", BASIC_JUMP_WHILE_POPPING_THE_NOSE, NOLLIE_VIDEO_URL, LocalDate.of(2020, Month.NOVEMBER, 30), user3);
    	Move move3User3 = new Move("Shuvit", "making board spin 180 degrees", SHUVIT_VIDEO_URL, LocalDate.of(2020, Month.NOVEMBER, 30), user3);

    	moveRepository.create(move1User1);

    	moveRepository.create(move1User2);
    	moveRepository.create(move2User2);

    	moveRepository.create(move1User3);
    	moveRepository.create(move2User3);
    	moveRepository.create(move3User3);

	}

    public static LocalDate nextMonday(LocalDate date) {
		int currentDay = date.getDayOfWeek().ordinal();
        LocalDate nextMonday = date.plusDays(7 - currentDay);

        return nextMonday;
	}
}
