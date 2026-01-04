#!/bin/bash

# Set environment variable for local development
export IMAGE_STORAGE_LOCATION="./images"
mkdir -p ./images

python3 main.py
