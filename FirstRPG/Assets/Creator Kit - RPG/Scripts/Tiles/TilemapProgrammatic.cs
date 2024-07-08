using System;
using System.IO;
using UnityEngine;
using UnityEngine.AddressableAssets;
using UnityEngine.ResourceManagement.AsyncOperations;
using UnityEngine.Tilemaps;

namespace Creator_Kit___RPG.Scripts.Tiles
{
    public class ProgrammaticWorld : MonoBehaviour
    {
        private const string Environment = "Assets/Creator Kit - RPG/Art/Sprites/Environment/{0}[{1}]";
        private const string JsonFilePath = "WorldTileData.json";
        
        private Tilemap _tilemap;
        private Grid _grid;
        private SceneData _sceneData;

        private void Start()
        {
            LoadTileData();
            CreateGrid();
            CreateTilemap();
            GenerateTilemap();
        }

        private void LoadTileData()
        {
            try
            {
                var tileData = File.ReadAllText(JsonFilePath);
                _sceneData = JsonUtility.FromJson<SceneData>(tileData);
            }
            catch (Exception e)
            {
                Debug.Log($"exception while load tile data {e}");
            }
        }

        private void GenerateTilemap()
        {
            foreach (var tilemaps in _sceneData.tilemaps)
            {
                foreach (var tile in tilemaps.tiles)
                {
                    {
                        var tileKey = string.Format(Environment, "Fence.png", tile.sprite);
                        Addressables.LoadAssetAsync<Sprite>(tileKey).Completed += handle =>
                        {
                            if (handle.Status == AsyncOperationStatus.Succeeded)
                            {
                                var pos = new Vector3Int(tile.position.x, tile.position.y, 0);
                                var sprite = handle.Result;
                                var tileObject = ScriptableObject.CreateInstance<Tile>();
                                tileObject.sprite = sprite;
                                _tilemap.SetTile(pos, tileObject);
                            }
                            else
                            {
                                Debug.LogError($"Failed to load sprite for key: {tileKey}");
                            }
                        };
                    }
                }
            }
        }


        private void CreateGrid()
        {
            Debug.Log("Create Grid GameObject");
            var gridObject = new GameObject("Grid", typeof(Grid));
            gridObject.transform.SetParent(transform);
            gridObject.transform.localPosition = Vector3.zero;
            _grid = gridObject.GetComponent<Grid>();
        }

        private void CreateTilemap()
        {
            Debug.Log("Create Tilemap GameObject");
            var tilemapObject = new GameObject("Tilemap", typeof(Tilemap), typeof(TilemapRenderer));
            tilemapObject.transform.SetParent(_grid.transform);
            tilemapObject.transform.localPosition = Vector3.zero;
            _tilemap = tilemapObject.GetComponent<Tilemap>();
        }
    }
}