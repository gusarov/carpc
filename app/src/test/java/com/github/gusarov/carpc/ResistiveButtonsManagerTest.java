package com.github.gusarov.carpc;

import com.github.gusarov.carpc.Commands.Command;
import com.github.gusarov.carpc.Commands.CommandManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class ResistiveButtonsManagerTest {

	CommandManager cm;
	ResistiveButtonsManager rbm;
	Command cmd1;
	Command cmd2;

	@Before
	public void Init() throws URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		cm = CommandManager.getInstance();
		rbm = new ResistiveButtonsManager();
		cmd1 = cm.getAllCommands().get(0);
		cmd2 = cm.getAllCommands().get(1);
		rbm.loadConfig(100, "LevelDown");
		rbm.loadConfig(200, "LevelUp");
		rbm.loadConfig(300, "LevelUp");
	}

	@Test
	public void Should_find_low1() {
		ResistiveButtonInfo info = rbm.getCommand(10);
		Assert.assertEquals(100, info.MainLevel);
	}

	@Test
	public void Should_find_low2() {
		ResistiveButtonInfo info = rbm.getCommand(100);
		Assert.assertEquals(100, info.MainLevel);
	}

	@Test
	public void Should_find_med1() {
		ResistiveButtonInfo info = rbm.getCommand(110);
		Assert.assertEquals(100, info.MainLevel);
	}

	@Test
	public void Should_find_med2() {
		ResistiveButtonInfo info = rbm.getCommand(190);
		Assert.assertEquals(200, info.MainLevel);
	}

	@Test
	public void Should_find_hig() {
		ResistiveButtonInfo info = rbm.getCommand(200);
		Assert.assertEquals(200, info.MainLevel);
	}

	@Test
	public void Should_find_hig2() {
		ResistiveButtonInfo info = rbm.getCommand(210);
		Assert.assertEquals(200, info.MainLevel);
	}

	@Test
	public void Should_find_more() {
		ResistiveButtonInfo info = rbm.getCommand(290);
		Assert.assertEquals(300, info.MainLevel);
	}


	@Test
	public void Should_find_more2() {
		ResistiveButtonInfo info = rbm.getCommand(300);
		Assert.assertEquals(300, info.MainLevel);
	}


	@Test
	public void Should_find_more3() {
		ResistiveButtonInfo info = rbm.getCommand(310);
		Assert.assertEquals(300, info.MainLevel);
	}

}
