#!/bin/bash

# Check if Google Chrome is installed
if ! command -v google-chrome &> /dev/null
then
    echo "Google Chrome is not installed. Downloading and installing now..."
    
    # Download the Google Chrome .deb package
    wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
    
    # Install the downloaded package
    sudo dpkg -i google-chrome-stable_current_amd64.deb
    
    # Fix any dependency issues
    sudo apt-get install -f
    
    echo "Google Chrome has been installed."
else
    echo "Google Chrome is already installed."
fi