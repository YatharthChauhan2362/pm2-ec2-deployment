#!/bin/bash


sudo systemctl stop jenkins

sudo apt purge jenkins -y
sudo apt autoremove -y


sudo rm /etc/apt/sources.list.d/jenkins.list
sudo rm /usr/share/keyrings/jenkins-keyring.asc


sudo apt update
