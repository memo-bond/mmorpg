using Creator_Kit___RPG.Scripts.Core;
using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Character
{
    public class OtherPlayer : BasePlayer
    {
        private Vector3 _destination = Vector3.zero;

        protected override void Start()
        {
            base.Start();
            transform.position = Position;
        }

        // protected override void Update()
        // {
        //     base.Update();
        //
        //     if (_destination == Vector3.zero) return;
        //     
        //     start = transform.position;
        //     end = start + nextMoveCommand;
        //     distance = (end - start).magnitude;
        //     
        //     Debug.Log($"Other player move nextMoveCommand ${nextMoveCommand} " +
        //               $"Start ${start} " +
        //               $"End {end} " +
        //               $"Destination {_destination} " +
        //               $"Vector3.Distance(end, _destination) {Vector3.Distance(start, _destination)}");
        //     
        //     if (Vector3.Distance(start, _destination) >= 1f)
        //     {
        //         state = State.Idle;
        //         nextMoveCommand = Vector3.zero;
        //         rigidbody2D.velocity = Vector2.zero;    
        //     }
        // }
        
        protected override void Update()
        {
            base.Update();

            if (_destination == Vector3.zero)
                return;

            var currentPosition = transform.position;

            // Calculate the end position after moving nextMoveCommand
            var endPosition = currentPosition + nextMoveCommand;
            
            Debug.Log($"Other player move " +
                      $"currentPosition ${currentPosition} " +
                      $"endPosition {endPosition} " +
                      $"Destination {_destination} " +
                      $"Vector3.Distance(endPosition, _destination) {Vector3.Distance(endPosition, _destination)}");

            // Check if we have reached or passed the destination
            if (Vector3.Distance(endPosition, _destination) <= nextMoveCommand.magnitude)
            {
                // If so, stop moving
                transform.position = _destination;
                state = State.Idle;
                nextMoveCommand = Vector3.zero;
                rigidbody2D.velocity = Vector2.zero;
                _destination = Vector3.zero; // Reset destination
            }
            else
            {
                // Move towards the destination
                transform.position = Vector3.MoveTowards(currentPosition, _destination, nextMoveCommand.magnitude * Time.deltaTime);
            }
        }

        public void MovePlayer(Vector3 move, Vector3 destination)
        {
            nextMoveCommand = move;
            _destination = destination;
        }
    }
}