using System;
using System.Collections.Generic;
using Google.Protobuf;
using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Core
{
    public class ProtoEventManager : MonoBehaviour
    {
        private static ProtoEventManager _instance;

        private Dictionary<ProtoEventName, Action<IMessage>> _eventDictionary;

        private static ProtoEventManager Instance
        {
            get
            {
                if (_instance) return _instance;
                _instance = FindObjectOfType<ProtoEventManager>();
                if (!_instance)
                {
                    var obj = new GameObject("ProtoEventManager");
                    _instance = obj.AddComponent<ProtoEventManager>();
                }
                _instance.Init();
                return _instance;
            }
        }

        private void Init()
        {
            _eventDictionary ??= new Dictionary<ProtoEventName, Action<IMessage>>();
        }

        public static void StartListening(ProtoEventName protoEventName, Action<IMessage> listener)
        {
            if (Instance._eventDictionary.TryGetValue(protoEventName, out var thisEvent))
            {
                thisEvent += listener;
                Instance._eventDictionary[protoEventName] = thisEvent;
            }
            else
            {
                thisEvent += listener;
                Instance._eventDictionary.Add(protoEventName, thisEvent);
            }
        }

        public static void StopListening(ProtoEventName protoEventName, Action<IMessage> listener)
        {
            if (!Instance._eventDictionary.TryGetValue(protoEventName, out var thisEvent)) return;
            thisEvent -= listener;
            if (thisEvent == null)
            {
                Instance._eventDictionary.Remove(protoEventName);
            }
            else
            {
                Instance._eventDictionary[protoEventName] = thisEvent;
            }
        }

        public static void TriggerEvent(ProtoEventName protoEventName, IMessage eventParam = null)
        {
            if (Instance._eventDictionary.TryGetValue(protoEventName, out var thisEvent))
            {
                thisEvent.Invoke(eventParam);
            }
        }
    }
}