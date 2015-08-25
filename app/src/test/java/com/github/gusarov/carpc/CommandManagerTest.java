package com.github.gusarov.carpc;

import com.github.gusarov.carpc.Commands.Command;
import com.github.gusarov.carpc.Commands.CommandManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class CommandManagerTest {

	CommandManager cm;

	@Before
	public void Init() throws URISyntaxException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		cm = CommandManager.getInstance();
	}

	@Test
	public void Should_have_cmd_man() throws IOException {
		Assert.assertNotNull(cm);
	}

	@Test
	public void Should_list_all_commands() throws IOException, URISyntaxException, IllegalAccessException, InstantiationException, ClassNotFoundException {
		List<Command> cmds = cm.getAllCommands();
		Assert.assertTrue(cmds.size() > 3);
		Assert.fail(cmds.get(0).getCode());
	}

}

