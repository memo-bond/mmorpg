using System;
using System.Collections.Generic;
using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Tiles
{
    
    [Serializable]
    public class SceneData
    {
        public List<TilemapData> tilemaps = new();
    }
    
    [Serializable]
    public class TilemapData
    {
        public string name;
        public List<MyTileData> tiles = new();
    }
    
    [Serializable]
    public class MyTileData
    {
        public string tileType;
        public string sprite;
        public Vector3Int position;
    }
}