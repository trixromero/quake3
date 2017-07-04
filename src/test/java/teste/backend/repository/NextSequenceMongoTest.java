package teste.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import teste.backend.service.NextSequenceService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NextSequenceMongoTest {
	
	@Autowired
	NextSequenceService nextSequenceMongoTest;
	
	@Test
	public void shouldIncreaseJustOne(){
		int first = nextSequenceMongoTest.getNextSequence("test-mongo");
		int second = nextSequenceMongoTest.getNextSequence("test-mongo");
		assertThat(first).isEqualTo(1);
		assertThat(second).isEqualTo(++first);
	}
	
	@Test
	public void shouldStartInOne(){
		int first = nextSequenceMongoTest.getNextSequence("test-mongo2");
		assertThat(first).isEqualTo(1);
	}

}
