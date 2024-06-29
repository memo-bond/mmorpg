using UnityEngine;
using UnityEngine.U2D;

namespace Creator_Kit___RPG.Scripts.Character
{
    public class BaseCharacter : MonoBehaviour
    {
        [SerializeField] private float speed = 1;
        [SerializeField] private float acceleration = 2;
        [SerializeField] public Vector3 nextMoveCommand;
        [SerializeField] protected Animator animator;
        [SerializeField] private bool flipX = false;

        protected new Rigidbody2D rigidbody2D;
        protected SpriteRenderer spriteRenderer;
        protected PixelPerfectCamera pixelPerfectCamera;

        protected Vector3 lastPosition;
        protected float positionUpdateThreshold = 0.2f;

        protected enum State
        {
            Idle,
            Moving
        }

        protected State state = State.Idle;
        protected Vector3 start, end;
        protected Vector2 currentVelocity;
        protected float startTime;
        protected float distance;
        protected float velocity;
        private static readonly int WalkX = Animator.StringToHash("WalkX");
        private static readonly int WalkY = Animator.StringToHash("WalkY");

        public uint Id { get; set; }
        public string Name { get; set; }
        public bool Main { get; set; }
        public Vector3 Position { get; set; }

        protected virtual void Awake()
        {
            rigidbody2D = GetComponent<Rigidbody2D>();
            spriteRenderer = GetComponent<SpriteRenderer>();
            pixelPerfectCamera = FindObjectOfType<PixelPerfectCamera>();
        }

        protected virtual void Start()
        {
            lastPosition = transform.position;
        }

        protected virtual void Update()
        {
            switch (state)
            {
                case State.Idle:
                    IdleState();
                    break;
                case State.Moving:
                    MoveState();
                    break;
            }
        }

        protected virtual void LateUpdate()
        {
            if (pixelPerfectCamera)
            {
                transform.position = pixelPerfectCamera.RoundToPixel(transform.position);
            }
        }

        private void IdleState()
        {
            if (nextMoveCommand != Vector3.zero)
            {
                start = transform.position;
                end = start + nextMoveCommand;
                distance = (end - start).magnitude;
                velocity = 0;
                UpdateAnimator(nextMoveCommand);
                nextMoveCommand = Vector3.zero;
                state = State.Moving;
            }
            else
            {
                rigidbody2D.velocity = Vector2.zero; // Stop the slipping
                UpdateAnimator(Vector3.zero); // Reset animator parameters
            }
        }

        protected virtual void MoveState()
        {
            velocity = Mathf.Clamp01(velocity + Time.deltaTime * acceleration);
            UpdateAnimator(nextMoveCommand);
            rigidbody2D.velocity = Vector2.SmoothDamp(rigidbody2D.velocity,
                nextMoveCommand * speed, ref currentVelocity, acceleration, speed);
            spriteRenderer.flipX = rigidbody2D.velocity.x >= 0;
        }

        protected void UpdateAnimator(Vector3 direction)
        {
            if (!animator) return;
            var walkX = direction.x < 0 ? -1 : direction.x > 0 ? 1 : 0;
            var walkY = direction.y < 0 ? 1 : direction.y > 0 ? -1 : 0;

            animator.SetInteger(WalkX, walkX);
            animator.SetInteger(WalkY, walkY);
        }

        public void MovePlayer(Vector3 nextMove)
        {
            nextMoveCommand = nextMove;
        }
    }
}