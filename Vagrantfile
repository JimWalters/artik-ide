Vagrant.configure(2) do |config|
  config.vm.box = "boxcutter/centos71-docker"
  config.vm.box_download_insecure = true
  config.vm.network :private_network, ip: "192.168.28.28"

  config.vm.define "artik" do |artik|
  end

  config.vm.provider "virtualbox" do |vb|
    vb.memory = "4096"
    vb.name = "artik-ide-vm"
  end

  config.vm.provision "shell", inline: <<-SHELL
    usermod -aG docker vagrant
    curl -H "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" -L -o jdk8-linux-x64.rpm "http://download.oracle.com/otn-pub/java/jdk/8u74-b02/jdk-8u74-linux-x64.rpm"
    yum localinstall -y jdk8-linux-x64.rpm
    curl -O "https://install.codenvycorp.com/artik/samsung-artik-ide-nightly.tar.gz"
    tar xvfz samsung-artik-ide-nightly.tar.gz
    sudo chown -R vagrant:vagrant *
    export JAVA_HOME=/usr
    echo vagrant | sudo -S -E -u vagrant /home/vagrant/eclipse-che-4.2.0-RC1-SNAPSHOT/bin/che.sh --remote:192.168.28.28 --skip:client -g run
  SHELL
end
