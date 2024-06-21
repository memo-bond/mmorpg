using System.Threading.Tasks;
using Creator_Kit___RPG.Scripts.Connection;
using NativeWebSocket;
using UnityEngine;
using UnityEngine.U2D;

namespace RPGM.Gameplay
{
    /// <summary>
    /// A simple controller for animating a 4 directional sprite using Physics.
    /// </summary>
    public class CharacterController2D : MonoBehaviour
    {
        [SerializeField]
        private bool online = false;
        
        [SerializeField]
        private ConnectionManager client;

        public float speed = 1;
        public float acceleration = 2;
        public Vector3 nextMoveCommand;
        public Animator animator;
        public bool flipX = false;

        new Rigidbody2D rigidbody2D;
        SpriteRenderer spriteRenderer;
        PixelPerfectCamera pixelPerfectCamera;

        private Vector3 lastPosition;
        private float positionUpdateThreshold = 0.1f;

        enum State
        {
            Idle, Moving
        }

        State state = State.Idle;
        Vector3 start, end;
        Vector2 currentVelocity;
        float startTime;
        float distance;
        float velocity;

        private bool main;
        private int id;
        private string name;
        private Vector3 position;
        
        public bool Main
        {
            get { return main; }
            set { main = value; }
        }
        
        public int Id
        {
            get { return id; }
            set { id = value; }
        }

        public string Name
        {
            get { return name; }
            set { name = value; }
        }

        public Vector3 Position
        {
            get { return position; }
            set { position = value; }
        }
        

        private async void Start()
        {
            main = true;
            await StartAsync();
        }

        private async Task StartAsync()
        {
            lastPosition = transform.position;

            if (online && main)
            {
                Debug.Log("Waiting For Connect To Server");
                while (client.State() != WebSocketState.Open)
                {
                    await Task.Delay(10);
                }
                Debug.Log("Joining To Server");
                id = Random.Range(1, 99);
                PlayerMessage msg = new()
                {
                    Join = new()
                    {
                        Id = id,
                        Name = "Lucas",
                        X = transform.position.x,
                        Y = transform.position.y
                    }
                };
                
                Debug.Log($"Player Join {msg}");
                await client.Send(msg);
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
        }

        private void MoveState()
        {
            velocity = Mathf.Clamp01(velocity + Time.deltaTime * acceleration);
            UpdateAnimator(nextMoveCommand);
            rigidbody2D.velocity = Vector2.SmoothDamp(rigidbody2D.velocity, nextMoveCommand * speed, ref currentVelocity, acceleration, speed);
            spriteRenderer.flipX = rigidbody2D.velocity.x >= 0 ? true : false;

            if (!online) return; // guard
            if (!(Vector3.Distance(transform.position, lastPosition) > positionUpdateThreshold)) return; // guard
            
            PlayerMessage msg = new()
            {
                Move = new()
                {
                    Id = id,
                    X = transform.position.x,
                    Y = -transform.position.y
                }
            };
            
            Debug.Log($"Player Move {msg}");
            _ = client.Send(msg);
            lastPosition = transform.position;
        }

        private void UpdateAnimator(Vector3 direction)
        {
            if (animator)
            {
                animator.SetInteger("WalkX", direction.x < 0 ? -1 : direction.x > 0 ? 1 : 0);
                animator.SetInteger("WalkY", direction.y < 0 ? 1 : direction.y > 0 ? -1 : 0);
            }
        }

        private void Update()
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

        private void LateUpdate()
        {
            if (pixelPerfectCamera)
            {
                transform.position = pixelPerfectCamera.RoundToPixel(transform.position);
            }
        }

        private void Awake()
        {
            rigidbody2D = GetComponent<Rigidbody2D>();
            spriteRenderer = GetComponent<SpriteRenderer>();
            pixelPerfectCamera = FindObjectOfType<PixelPerfectCamera>();
        }

        private async void OnApplicationQuit()
        {
            PlayerMessage msg = new()
            {
                Quit = new()
                {
                    Id = id
                }
            };
            await client.Send(msg);
        }
    }
}