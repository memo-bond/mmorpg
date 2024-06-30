using System;
using System.Collections.Generic;
using Creator_Kit___RPG.Scripts.Character;
using RPGM.Gameplay;
using RPGM.UI;
using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Gameplay
{
    /// <summary>
    /// This class provides all the data you need to control and change gameplay.
    /// </summary>
    [Serializable]
    public class GameModel
    {
        public MainPlayer player;
        public DialogController dialog;
        public InputController input;
        public InventoryController inventoryController;
        public MusicController musicController;

        private Dictionary<GameObject, HashSet<string>> _conversations = new();

        private Dictionary<string, int> _inventory = new();
        private Dictionary<string, Sprite> _inventorySprites = new();

        private HashSet<string> _storyItems = new();

        public IEnumerable<string> InventoryItems => _inventory.Keys;

        public Sprite GetInventorySprite(string name)
        {
            Sprite s;
            _inventorySprites.TryGetValue(name, out s);
            return s;
        }

        public int GetInventoryCount(string name)
        {
            int c;
            _inventory.TryGetValue(name, out c);
            return c;
        }

        public void AddInventoryItem(InventoryItem item)
        {
            int c = 0;
            _inventory.TryGetValue(item.name, out c);
            c += item.count;
            _inventorySprites[item.name] = item.sprite;
            _inventory[item.name] = c;
            inventoryController.Refresh();
        }

        public bool HasInventoryItem(string name, int count = 1)
        {
            int c = 0;
            _inventory.TryGetValue(name, out c);
            return c >= count;
        }

        public bool RemoveInventoryItem(InventoryItem item, int count)
        {
            int c = 0;
            _inventory.TryGetValue(item.name, out c);
            c -= count;
            if (c < 0) return false;
            _inventory[item.name] = c;
            inventoryController.Refresh();
            return true;
        }

        public void RegisterStoryItem(string ID)
        {
            _storyItems.Add(ID);
        }

        public bool HasSeenStoryItem(string ID)
        {
            return _storyItems.Contains(ID);
        }

        public void RegisterConversation(GameObject owner, string id)
        {
            if (!_conversations.TryGetValue(owner, out HashSet<string> ids))
                _conversations[owner] = ids = new HashSet<string>();
            ids.Add(id);
        }

        public bool HasHadConversationWith(GameObject owner, string id)
        {
            if (!_conversations.TryGetValue(owner, out HashSet<string> ids))
                return false;
            return ids.Contains(id);
        }

        public bool HasMet(GameObject owner)
        {
            return _conversations.ContainsKey(owner);
        }
    }
}