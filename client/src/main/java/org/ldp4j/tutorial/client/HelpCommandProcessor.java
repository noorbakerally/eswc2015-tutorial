/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the LDP4j Project:
 *     http://www.ldp4j.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2014-2016 Center for Open Middleware.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Artifact    : org.ldp4j.tutorial.client:eswc-2015-client:1.0.0
 *   Bundle      : eswc-2015-client-1.0.0.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.ldp4j.tutorial.client;

final class HelpCommandProcessor extends AbstractCommandProcessor {

	@Override
	public boolean execute(CommandContext options) {
		if(options.hasTarget()) {
			Command cmd=Command.fromString(options.target());
			if(cmd==null) {
				console().error("ERROR: Unknown command '%s'%n",options.target());
			} else {
				console().message("Shell comand:%n");
				console().metadata("- %s : %s%n", cmd.commandName(),cmd.commandDescription());
			}
		} else {
			console().message("Available commands:%n");
			for(Command cmd:Command.values()) {
				console().metadata("- %s : %s%n", cmd.commandName(),cmd.commandDescription());
			}
		}
		return true;
	}

	@Override
	public boolean canExecute(CommandContext context) {
		return !context.hasOptions();
	}

}