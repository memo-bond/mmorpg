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
        private const int Width = 20;
        public int height = 10;

        private readonly List<string> _tileAddressKeys = new()
            { "Assets/Creator Kit - RPG/Art/Sprites/Environment/Fence.png[Fence_4]" };

        void Start()
        {
            CreateTilemap();
            GenerateTilemap();
        }

        private void CreateTilemap()
        {
            Debug.Log("Create Tilemap GameObject");
            var tilemapObject = new GameObject("Tilemap", typeof(Tilemap), typeof(TilemapRenderer));
            tilemapObject.transform.SetParent(transform);
            tilemapObject.transform.localPosition = Vector3.zero;
            _tilemap = tilemapObject.GetComponent<Tilemap>();
        }

        private void GenerateTilemap()
        {
            for (var x = 0; x < Width; x++)
            {
                var tileKey = _tileAddressKeys[Random.Range(0, _tileAddressKeys.Count)];
                var x1 = x;
                Addressables.LoadAssetAsync<Sprite>(tileKey).Completed += handle =>
                {
                    if (handle.Status == AsyncOperationStatus.Succeeded)
                    {
                        var pos = new Vector3Int(x1, 0, 0);
                        Debug.Log($"Add tile at {pos}");
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