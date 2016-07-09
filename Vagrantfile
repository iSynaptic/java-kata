# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.box_version = "20160707.0.1"
  config.vm.network "public_network"

  config.vm.provider "virtualbox" do |vb|
    vb.memory = "4096"
  end

  config.vm.provision "shell", inline: <<-SHELL
    apt-get install -y git traceroute
    
    wget -qO- https://get.docker.com/ | sh
    docker run --rm hello-world
  SHELL
end