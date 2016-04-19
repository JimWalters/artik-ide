# Samsung ARTIK&trade; IDE Powered by Eclipse Che
[![Join the chat at https://gitter.im/eclipse/che](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/eclipse/che?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Eclipse License](http://img.shields.io/badge/license-Eclipse-brightgreen.svg)](https://github.com/codenvy/che/blob/master/LICENSE)
[![Build Status](http://ci.codenvy-dev.com/jenkins/buildStatus/icon?job=che-ci-master)](http://ci.codenvy-dev.com/jenkins/job/che-ci-master)

https://www.eclipse.org/che/artik/. IDE for Samsung ARTIK platform. 

![Eclipse Che](https://www.eclipse.org/che/images/hero-home.png "Eclipse Che")

### Universal Workspaces
Worskpaces run on your desktop, in the cloud, or on the device.

### Device Discovery
Discover devices and manage connections over SSH. Manage users, passwords and persistent connections.

### Project Structure
Create multiple projects mapped to different source repositories. Projects can have sub-project structures and typing to impbue different behaviors.

### Programming Language Intelligence
Intellisense for multiple languages. Local and on-device debugging for Java, Python, Go, C, and C++.

### Workspace Runtimes
Workspace runtimes are Docker-powered. Use our all-in-one stacks, pull from any registry, or author your own. Snapshot and embed runtimes into ... [Read More](https://www.eclipse.org/che/features/#docker-powered)

### Cloud IDE
A no-installation browser IDE accessible from any local or remote device. Thin, fast, and beautiful. [Read More](https://www.eclipse.org/che/features/#cloud-ide)

### Getting Started
The ARTIK IDE can be installed on any OS that supports Java 1.8 - desktop, server or cloud, and Maven 3.3.1. It has been tested on Ubuntu, Linux, MacOS and Windows. 

Follow the [step by step guide](http://eclipse.org/che/artik/getting-started.html) to install ARTIK IDE from our binaries.

### License
ARTIK IDE is open sourced under the Eclipse Public License 1.0.

### Dependencies
* Docker 1.8+
* Maven 3.3.1+
* Java 1.8

### Clone

```sh
git clone https://github.com/codenvy/artik-ide.git
```
If master is unstable, checkout the latest tagged version.

### Build and Run
```sh
cd artik-ide
mvn clean install

# A new assembly is placed in:
cd assembly/assembly-main/target/eclipse-che-<version>/eclipse-che-<version>

# Executable files are:
bin/che.sh
bin/che.bat
```
The ARTIK IDE will be available at ```localhost:8080```.

### Build Submodules
Building `/assembly` pulls already-built libraries for Eclipse Che `/core`, `/plugins`, and `/dashboard` from our Nexus repository. You can customize the Eclipse Che core, plugins, and dashboard directly by cloning [Eclipse Che](http://github.com/eclipse/che).

### Run ARTIK IDE as a Server
If you want to run the ARTIK IDE as a server, there are additional Eclipse Che flags that you may need to configure. Please see the [usage and networking documentation](https://eclipse-che.readme.io/docs/usage).

### Custom Eclipse Che Assembly
The ARTIK IDE is a custom [Eclipse Che assembly](https://eclipse-che.readme.io/docs/assemblies). On its own, Eclipse Che provides its own IDE. This repository rebuilds its own assembly using Eclipse Che as the baseline and then overrides the assembly instructions (in `/assembly` directory) and adds additional ARTIK-specific plugins (in the `/plugins`) directory. The assembly builds binaries that use base binaries from Eclipse Che that are stored in Che's nexus repository.

### Modules
These modules make up the ARTIK IDE assembly hosted at `http://github.com/codenvy/artik-ide`.
```
/artik-ide
/artik-ide/assembly                                       # Generates binary assemblies of Che
/artik-ide/assembly/assembly-main                         # Final packaging phase
/artik-ide/assembly/assembly-ide-war                      # Creates the IDE.war from plug-ins & core
/artik-ide/assembly/assembly-machine-war                  # Creates the agent WAR from plug-ins & core
/artik-ide/assembly/assembly-machine-server               # Creates the agent server that goes into ws
/artik-ide/plugins/plugin-artik-ide                       # Creates a new parent IDE, overriding Che's IDE
/artik-ide/plugins/plugin-artik-server                    # Creates custom ARTIK machine, used for workspaces
/artik-ide/plugins/plugin-artik-shared                    # Classes shared between IDE and server
```

These modules make up Eclipse Che core, which is imported to create ARTIK IDE hosted at `http://github.com/eclipse/che`.
```
/che/core                                                 # Libraries shared among server, agents, and plugins
/che/dashboard                                            # AngularJS app for managing Che
/che/plugins                                              # IDE & agent plug-ins
/che/wsmaster                                             # Libraries used by the Che server
/che/wsagent                                              # Libraries used by agents installed into workspaces

/che-lib                                                  # Forked dependencies that require mods
/che-lib/swagger
/che-lib/terminal
/che-lib/websocket
/che-lib/pty
/che-lib/che-tomcat8-slf4j-logback

# /che and /che-lib depend upon /che-dependencies
/che-dependencies                                          # Maven dependencies used by che
/che-dev                                                   # Code style and license header

# /che-dependencies and /che-dev depend upon /che-parent
/che-parent                                                # Maven plugins and profiles
```

### Engage
* **Contribute:** We love pull requests! [Contribute to ARTIK IDE](https://github.com/codenvy/artik-ide/blob/master/CONTRIBUTING.md) or [contribute to Eclipse Che](https://github.com/eclipse/che/blob/master/CONTRIBUTING.md).
* **Customize:** [Runtimes, stacks, commands, assemblies, extensions, plug-ins](https://github.com/eclipse/che/blob/master/CUSTOMIZING.md).
* **Support:** You can report bugs using [GitHub issues](https://github.com/eclipse/che/issues).
* **Developers:** Plug-in developers can get help on [Eclipse Che's dev list](email:che-dev@eclipse.org). 
* **Website:** [eclipse.org/che](https://eclipse.org/che/artik).
