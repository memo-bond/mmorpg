using System;
using System.Collections.Generic;
using Google.Protobuf;
using UnityEngine;

public class EventManager : MonoBehaviour
{
    private static EventManager instance;

    private Dictionary<string, Action<IMessage>> eventDictionary;

    public static EventManager Instance
    {
        get
        {
            if (!instance)
            {
                instance = FindObjectOfType<EventManager>();
                if (!instance)
                {
                    GameObject obj = new GameObject("EventManager");
                    instance = obj.AddComponent<EventManager>();
                }
                instance.Init();
            }
            return instance;
        }
    }

    private void Init()
    {
        if (eventDictionary == null)
        {
            eventDictionary = new Dictionary<string, Action<IMessage>>();
        }
    }

    public static void StartListening(string eventName, Action<IMessage> listener)
    {
        Action<IMessage> thisEvent;
        if (Instance.eventDictionary.TryGetValue(eventName, out thisEvent))
        {
            thisEvent += listener;
            Instance.eventDictionary[eventName] = thisEvent;
        }
        else
        {
            thisEvent += listener;
            Instance.eventDictionary.Add(eventName, thisEvent);
        }
    }

    public static void StopListening(string eventName, Action<IMessage> listener)
    {
        if (instance == null) return;
        Action<IMessage> thisEvent;
        if (Instance.eventDictionary.TryGetValue(eventName, out thisEvent))
        {
            thisEvent -= listener;
            if (thisEvent == null)
            {
                Instance.eventDictionary.Remove(eventName);
            }
            else
            {
                Instance.eventDictionary[eventName] = thisEvent;
            }
        }
    }

    public static void TriggerEvent(string eventName, IMessage eventParam = null)
    {
        Action<IMessage> thisEvent;
        if (Instance.eventDictionary.TryGetValue(eventName, out thisEvent))
        {
            thisEvent.Invoke(eventParam);
        }
    }
}
