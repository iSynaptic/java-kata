# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.box_version = "20160707.0.1"
  
  config.vm.provider "virtualbox" do |vb|
    vb.memory = "4096"
  end

  config.vm.provision "shell", inline: <<-SHELL
    sudo add-apt-repository ppa:openjdk-r/ppa -y
    sudo apt-get update
    
    sudo apt-get -y install unzip openjdk-8-jdk

    cd /usr/lib
    sudo curl -fl https://downloads.gradle.org/distributions/gradle-2.13-bin.zip -o gradle-bin.zip
    sudo echo "$GRADLE_SHA gradle-bin.zip" | sha256sum -c -
    sudo unzip "gradle-bin.zip"
    sudo ln -s "/usr/lib/gradle-2.13/bin/gradle" /usr/bin/gradle
    sudo rm "gradle-bin.zip"

    echo 'export PATH=/usr/lib/gradle:$PATH' >> ~/.bashrc  

	  sudo rm -rf /var/lib/apt/lists/*
  SHELL
end