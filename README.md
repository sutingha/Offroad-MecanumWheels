Mecanum Wheels
=======

A NeoForge addon for Create: Aeronautics that adds 8 Mecanum wheels
for omnidirectional vehicle movement.
一个基于Minecraft neoforge 1.21.1版本的模组，旨在为航空学的越野部分添加麦克纳姆轮，
灵感来源于机器人比赛(FTC...?)

Features
-------

- Adds 8 different kinds of wheels.
  -8个型号的麦轮
- the mecanum wheels,45°
- THE CCW wheels means thr mirrored mecanum wheels,-45°
  -用nbt决定麦轮辊子方向显然太优雅了，所以直接把两个方向的麦轮作为物品好了
- In-game config via `/mecanum config`
  -添加了”/mecamum config 力组 操作方式 值“作为对麦轮力配置的指令，
  显然越野学原版对侧向力进行了大削，所以你可以试着把side改到0.6
- Converting between CW and CCW: put a Mecanum wheel in any crafting grid
  -两个镜像的麦轮现在可以相互合成转换了（来自@MaveTheMaverick的提议）

Dependencies
-------

- NeoForge 1.21.1
- Create
- Create: Aeronautics
  依赖于MC的新锻造1.21.1，以及航空学

License
-------

MIT. See [LICENSE](LICENSE).
持MIT协议