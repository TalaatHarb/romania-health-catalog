#!/bin/bash

# Check if Microsoft Edge is installed
if ! command -v microsoft-edge &> /dev/null
then
    echo "Microsoft Edge is not installed. Downloading and installing now..."
    
    # Download the Microsoft Edge .deb package
    wget https://packages.microsoft.com/repos/edge/pool/main/m/microsoft-edge-stable/microsoft-edge-stable_128.0.2739.42-1_amd64.deb
    
    # Install the downloaded package
    sudo dpkg -i microsoft-edge-stable_128.0.2739.42-1_amd64.deb
    
    # Fix any dependency issues
    sudo apt-get install -f
    
    echo "Microsoft Edge has been installed."
else
    echo "Microsoft Edge is already installed."
fi
