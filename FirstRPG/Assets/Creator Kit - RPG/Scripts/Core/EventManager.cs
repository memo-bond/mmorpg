using System;
using System.Collections.Generic;
using UnityEngine;
using Object = UnityEngine.Object;

namespace Creator_Kit___RPG.Scripts.Core
{
    public class EventManager : MonoBehaviour
    {
        private static EventManager _instance;

        private Dictionary<EventName, Action<Object>> _eventDictionary;

        private static EventManager Instance
        {
            get
            {
                if (_instance) return _instance;
                _instance = FindObjectOfType<EventManager>();
                if (!_instance)
                {
                    var obj = new GameObject("EventManager");
                    _instance = obj.AddComponent<EventManager>();
                }

                _instance.Init();
                return _instance;
            }
        }

        private void Init()
        {
            _eventDictionary ??= new Dictionary<EventName, Action<Object>>();
        }

        public static void StartListening(EventName eventName, Action<Object> listener)
        {
            if (Instance._eventDictionary.TryGetValue(eventName, out var thisEvent))
            {
                thisEvent += listener;
                Instance._eventDictionary[eventName] = thisEvent;
            }
            else
            {
                thisEvent += listener;
                Instance._eventDictionary.Add(eventName, thisEvent);
            }
        }

        public static void StopListening(EventName eventName, Action<Object> listener)
        {
            if (!Instance._eventDictionary.TryGetValue(eventName, out var thisEvent)) return;
            thisEvent -= listener;
            if (thisEvent == null)
            {
                Instance._eventDictionary.Remove(eventName);
            }
            else
            {
                Instance._eventDictionary[eventName] = thisEvent;
            }
        }

        public static void TriggerEvent(EventName eventName, Object eventParam)
        {
            if (Instance._eventDictionary.TryGetValue(eventName, out var thisEvent))
            {
                thisEvent.Invoke(eventParam);
            }
        }
    }
}