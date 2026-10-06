#!/bin/bash

# Check if Firefox is installed
if ! command -v firefox &> /dev/null
then
    echo "Firefox is not installed. Downloading and installing now..."
    
    # Update package lists
    sudo apt-get update
    
    # Install Firefox
    sudo apt-get install -y firefox
    
    echo "Firefox has been installed."
else
    echo "Firefox is already installed."
fi
