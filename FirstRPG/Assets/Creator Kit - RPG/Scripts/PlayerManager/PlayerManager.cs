using System.Collections.Generic;
using RPGM.Gameplay;
using UnityEngine;

public class PlayerManager : MonoBehaviour
{
    [SerializeField]
    private GameObject playerPrefab;
    private readonly Dictionary<int, CharacterController2D> players = new();

    public void AddPlayer(int id, string name, Vector3 position, bool isLocalPlayer = false)
    {
        if (players.ContainsKey(id))
        {
            Debug.LogWarning("Player with this ID already exists!");
            return;
        }

        var player = Instantiate(playerPrefab, position, Quaternion.identity);
        var controller = player.GetComponent<CharacterController2D>();

        controller.Id = id;
        controller.Name = name;
        controller.Position = position;

        players.Add(id, controller);
    }

    public void UpdatePlayerPosition(int id, Vector3 position)
    {
        if (players.TryGetValue(id, out CharacterController2D controller))
        {
            controller.transform.position = position;
        }
    }

    public void RemovePlayer(int id)
    {
        if (players.TryGetValue(id, out CharacterController2D controller))
        {
            Destroy(controller.gameObject);
            players.Remove(id);
        }
    }
}
