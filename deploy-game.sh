VERSION=$1
WORK_DIR=built/releases

# copy server.py to build
cp unity-server.py $WORK_DIR/$VERSION/server.py

cd $WORK_DIR

zip -r $VERSION.zip $VERSION

# copy to remote server
scp $VERSION.zip be:~/game/unity/mmorpg.zip

# start unity server
ssh be "bash ~/game/unity/start.sh $VERSION"