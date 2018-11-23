package com.liang.kafka;

import org.jasypt.encryption.StringEncryptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaSenderApplicationTests {
	@Autowired
	StringEncryptor encryptor;
	@Test
	public void contextLoads() {
	}
	@Test
	public void getPass() {
		String password = encryptor.encrypt("DBpassword");
		String s1 = encryptor.encrypt("root");
		String s2 = encryptor.encrypt("Ey762AbdWa");
		System.err.println(password+"----------------");
		System.err.println(s1+"/----------------");
		System.err.println(s2+"/----------------");
		Assert.assertTrue(password.length() > 0);
	}


}
