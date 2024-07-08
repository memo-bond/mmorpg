using System;
using System.IO;
using Creator_Kit___RPG.Scripts.Tiles;
using UnityEngine;
using UnityEngine.Tilemaps;

namespace Creator_Kit___RPG.Scripts.plugins
{
    public class SaveJson : MonoBehaviour
    {
        private void Start()
        {
            // Path to save the JSON file
            var filePath = Path.Combine(Application.dataPath, "WorldTileData.json");
            var sceneData = new SceneData();

            // Find the "World" GameObject
            var obj = GameObject.Find("World");
            if (obj)
            {
                Debug.Log($"obj {obj}");
                Debug.Log($"obj transform {obj.transform.position}");

                TraverseHierarchy(obj.transform, sceneData);
            }

            // Serialize the scene data to JSON
            var json = JsonUtility.ToJson(sceneData);

            // Write the JSON data to a file
            File.WriteAllText(filePath, json);

            Debug.Log("Tile data saved to " + filePath);
        }
    
        private static void GetData(GameObject node, SceneData sceneData)
        {
            try
            {
                var tilemap = node.GetComponent<Tilemap>();
                if (!tilemap) return;
                // Collect tile data from the tilemap
                var tilemapData = new TilemapData();
                tilemapData.name = tilemap.name;
                foreach (Vector3Int pos in tilemap.cellBounds.allPositionsWithin)
                {
                    TileBase tile = tilemap.GetTile(pos);
                    if (!tile) continue;
                    TileData tileData = new();
                    tile.GetTileData(pos, tilemap, ref tileData);
                    MyTileData myTileData = new();
                    myTileData.tileType = tile.name;
                    myTileData.position = pos;
                    if (tileData.sprite)
                        myTileData.sprite = tileData.sprite.name;
                    tilemapData.tiles.Add(myTileData);
                }

                sceneData.tilemaps.Add(tilemapData);
            }
            catch (Exception e)
            {
                Debug.Log($"{e}");
            }
        }
    
    
        private static void TraverseHierarchy(Transform parent, SceneData sceneData)
        {
            foreach (Transform child in parent)
            {
                var tilemap = child.GetComponent<Tilemap>();
                if (tilemap != null)
                {
                    // Collect tile data from the tilemap
                    var tilemapData = new TilemapData
                    {
                        name = tilemap.name
                    };
                    foreach (var pos in tilemap.cellBounds.allPositionsWithin)
                    {
                        var tile = tilemap.GetTile(pos);
                        if (!tile) continue;
                        TileData tileData = new();
                        tile.GetTileData(pos, tilemap, ref tileData);
                        MyTileData myTileData = new();
                        myTileData.tileType = tile.name;
                        myTileData.position = pos;
                        if (tileData.sprite)
                            myTileData.sprite = tileData.sprite.name;
                        tilemapData.tiles.Add(myTileData);
                    }

                    sceneData.tilemaps.Add(tilemapData);
                }

                // Recursive call to traverse children
                TraverseHierarchy(child, sceneData);
            }
        }
    }
}