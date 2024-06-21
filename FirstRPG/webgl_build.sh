#!/bin/bash

UNITY_PATH="/Applications/Unity/Hub/Editor/2022.3.28f1/Unity.app/Contents/MacOS/Unity"
PROJECT_PATH="/Users/lamle/Development/memobond/mmorpg/FirstRPG"
LOG_FILE="$PROJECT_PATH/build_log.txt"

# Check if Unity is already running with the project open
if pgrep -f "Unity.*$PROJECT_PATH" > /dev/null; then
    echo "Another Unity instance is running with this project open. Please close it first."
    exit 1
fi


$UNITY_PATH -quit -batchmode -projectPath $PROJECT_PATH -executeMethod WebGLBuilder.Build -logFile $PROJECT_PATH/build_log.txt

# Check if the build was successful
if [ $? -eq 0 ]; then
    echo "Build completed successfully."
else
    echo "Build failed. Check the log file for details: $LOG_FILE"
    exit 1
fi