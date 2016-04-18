# Samsung ARTIK&trade; IDE Powered by Eclipse Che
[![Join the chat at https://gitter.im/eclipse/che](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/eclipse/che?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Eclipse License](http://img.shields.io/badge/license-Eclipse-brightgreen.svg)](https://github.com/codenvy/che/blob/master/LICENSE)
[![Build Status](http://ci.codenvy-dev.com/jenkins/buildStatus/icon?job=che-ci-master)](http://ci.codenvy-dev.com/jenkins/job/che-ci-master)

TODO: Place with ARTIK IDE Home Page
https://www.eclipse.org/che/. Next-generation Eclipse IDE. Open source workspace server and cloud IDE.

![Eclipse Che](https://www.eclipse.org/che/images/hero-home.png "Eclipse Che")

### Workspaces With Runtimes
Workspaces are composed of projects and runtimes. Create portable and moavable workspaces that run anywhere, anytime in the cloud or on your desktop ... [Read More](https://www.eclipse.org/che/features/#new-workspace)

### Collaborative Workspace Server
Host Eclipse Che as a workspace server. Share tools, runtime and programming services across workspaces and teams. Control workspaces with REST APIs ... [Read More](https://www.eclipse.org/che/features/#collaborative)

### Docker-Powered Environments
Workspace runtimes are Docker-powered. Use our all-in-one stacks, pull from any registry, or author your own. Snapshot and embed runtimes into ... [Read More](https://www.eclipse.org/che/features/#docker-powered)

### Cloud IDE
A no-installation browser IDE and IOE accessible from any local or remote device. Thin, fast, and beautiful - it's the IDE our own engineers wanted ... [Read More](https://www.eclipse.org/che/features/#cloud-ide)

### Getting Started
Che can be installed on any OS that supports Java 1.8 - desktop, server or cloud, and Maven 3.3.1. It has been tested on Ubuntu, Linux, MacOS and Windows. 

Follow the [step by step guide](http://eclipse.org/che/getting-started/) to install Che from our binaries.

### License
Che is open sourced under the Eclipse Public License 1.0.

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
cd artik-ide/assembly/assembly-main/
mvn clean install

# A new assembly is placed in:
cd artik-ide/assembly/assembly-main/target/eclipse-che-<version>/eclipse-che-<version>

# Executable files are:
bin/che.sh
bin/che.bat
```
The ARTIK IDE will be available at ```localhost:8080```.

### Build Submodules
Building `/assembly` pulls already-built libraries for Eclipse Che `/core`, `/plugins`, and `/dashboard` from our Nexus repository. You can customize the Eclipse Che core, plugins, and dashboard directly by cloning [Eclipse Che](http://github.com/eclipse/che).

### Run ARTIK IDE as a Server
If you want to run the ARTIK IDE as a server, there are additional Eclipse Che flags that you may need to configure. Please see the [usage and networking documentation](https://eclipse-che.readme.io/docs/usage).

### Engage
* **Contribute:** We accept pull requests. Please see [how to contribute] (https://github.com/codenvy/che/blob/master/CONTRIBUTING.md).
* **Customize:** [Runtimes, stacks, commands, assemblies, extensions, plug-ins](https://github.com/eclipse/che/blob/master/CUSTOMIZING.md).
* **Support:** You can report bugs using GitHub issues.
* **Developers:** Plug-in developers can get API help at [che-dev@eclipse.org](email:che-dev@eclipse.org). 
* **Website:** [eclipse.org/che](https://eclipse.org/che).
