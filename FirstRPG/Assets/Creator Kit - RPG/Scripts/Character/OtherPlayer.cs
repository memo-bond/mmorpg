using System.Collections;
using System.Collections.Generic;
using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Character
{
    public class OtherPlayer : BasePlayer
    {
        private readonly Queue<Vector3> _destinations = new();
        private const float MoveSpeed = 5f;
        private bool _isMoving;

        protected override void Start()
        {
            base.Start();
            transform.position = Position;
        }

        public void MovePlayer(Vector3 move, Vector3 destination)
        {
            _destinations.Enqueue(move);
            state = State.Moving;

            if (!_isMoving)
            {
                StartCoroutine(MoveAlongPath());
            }
        }

        private IEnumerator MoveAlongPath()
        {
            _isMoving = true;
            while (_destinations.Count > 0)
            {
                var destination = _destinations.Dequeue();
                nextMoveCommand = destination;
                yield return new WaitForSeconds(0.1f);
            }
            nextMoveCommand = Vector3.zero;

            _isMoving = false;
        }
    }
}