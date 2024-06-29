using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Character
{
    public class OtherPlayer : BaseCharacter
    {
        protected override void Start()
        {
            base.Start();
            var characterAnimator = GameObject.Find("Character.controller");
            Debug.Log($"characterAnimator {characterAnimator}");
            transform.position = Position;
        }

        protected override void Update()
        {
            base.MoveState();
        }
    }
}