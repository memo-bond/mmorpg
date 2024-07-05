using System.Collections.Generic;
using UnityEngine;
using UnityEngine.AddressableAssets;
using UnityEngine.ResourceManagement.AsyncOperations;
using UnityEngine.Tilemaps;

namespace Creator_Kit___RPG.Scripts.Tiles
{
    public class ProgrammaticWorld : MonoBehaviour
    {
        private Tilemap _tilemap;
        private Grid _grid;

        private readonly List<string> _tileAddressKeys = new()
            { "Assets/Creator Kit - RPG/Art/Sprites/Environment/Fence.png[Fence_4]" };

        private void Start()
        {
            CreateGrid();
            CreateTilemap();
            GenerateTilemap();
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

        private void GenerateTilemap()
        {
            var numFences = 10;
            for (var i = 0; i < numFences; i++)
            {
                var tileKey = _tileAddressKeys[0];
                var localX = i;

                Addressables.LoadAssetAsync<Sprite>(tileKey).Completed += handle =>
                {
                    if (handle.Status == AsyncOperationStatus.Succeeded)
                    {
                        var pos = new Vector3Int(localX, 0, 0);
                        var sprite = handle.Result;
                        var tile = ScriptableObject.CreateInstance<Tile>();
                        tile.sprite = sprite;
                        _tilemap.SetTile(pos, tile);
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