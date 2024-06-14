using System.IO;
using System.Threading.Tasks;
using UnityEngine;
using UnityEngine.U2D;
using Google.Protobuf;
using System.Collections;
using NativeWebSocket;

namespace RPGM.Gameplay
{
    /// <summary>
    /// A simple controller for animating a 4 directional sprite using Physics.
    /// </summary>
    public class CharacterController2D : MonoBehaviour
    {
        [SerializeField]
        private bool online = false;

        private WebSocketClient client;

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

        private async void Start()
        {
            await StartAsync();
        }

        private async Task StartAsync()
        {
            lastPosition = transform.position;

            if (online)
            {
                client = new WebSocketClient("ws://localhost:6666/ws");
                Debug.Log("Client state 1 " + client.State());

                await Connect();
                
                Debug.Log("Client state 2 " + client.State());

                PlayerMessage msg = new()
                {
                    Join = new()
                    {
                        Id = 1,
                        Name = "Lucas",
                        X = transform.position.x,
                        Y = transform.position.y
                    }
                };
                
                client.Send(msg);
            }
        }

        private async Task Connect()
        {
            _ = client.Connect();

            while (client.State() != WebSocketState.Open)
            {
                await Task.Delay(10);
            }
        }

        void IdleState()
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

        void MoveState()
        {
            velocity = Mathf.Clamp01(velocity + Time.deltaTime * acceleration);
            UpdateAnimator(nextMoveCommand);
            rigidbody2D.velocity = Vector2.SmoothDamp(rigidbody2D.velocity, nextMoveCommand * speed, ref currentVelocity, acceleration, speed);
            spriteRenderer.flipX = rigidbody2D.velocity.x >= 0 ? true : false;

            if (online)
            {
                // send to server
                if (Vector3.Distance(transform.position, lastPosition) > positionUpdateThreshold)
                {
                    PlayerMessage msg = new()
                    {
                        Move = new()
                        {
                            Id = 1,
                            X = transform.position.x,
                            Y = transform.position.y
                        }
                    };

                    byte[] bytes;
                    using (var stream = new MemoryStream())
                    {
                        msg.WriteTo(stream);
                        bytes = stream.ToArray();
                    }
                    client.Send(msg);

                    lastPosition = transform.position;
                }
            }
        }

        void UpdateAnimator(Vector3 direction)
        {
            if (animator)
            {
                animator.SetInteger("WalkX", direction.x < 0 ? -1 : direction.x > 0 ? 1 : 0);
                animator.SetInteger("WalkY", direction.y < 0 ? 1 : direction.y > 0 ? -1 : 0);
            }
        }

        void Update()
        {
#if !UNITY_WEBGL || UNITY_EDITOR
            client.DispatchMessageQueue();
#endif
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

        void LateUpdate()
        {
            if (pixelPerfectCamera != null)
            {
                transform.position = pixelPerfectCamera.RoundToPixel(transform.position);
            }
        }

        void Awake()
        {
            rigidbody2D = GetComponent<Rigidbody2D>();
            spriteRenderer = GetComponent<SpriteRenderer>();
            pixelPerfectCamera = GameObject.FindObjectOfType<PixelPerfectCamera>();
        }

        private async void OnApplicationQuit()
        {
            await client.Close();
        }
    }
}