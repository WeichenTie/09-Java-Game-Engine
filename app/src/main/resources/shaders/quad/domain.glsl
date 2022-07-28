#version 410 core
layout(triangles, equal_spacing, ccw) in;

uniform mat4 u_Projection;
uniform mat4 u_View;
uniform mat4 u_Model;

vec3 interpolate(vec3 v0, vec3 v1, vec3 v2) {
  return gl_TessCoord.x * v0 + gl_TessCoord.y * v1 + gl_TessCoord.z * v2;
}

void main() {
	gl_Position =u_Projection * u_View * u_Model * vec4(
		interpolate(
			vec3(gl_in[0].gl_Position),
			vec3(gl_in[1].gl_Position),
			vec3(gl_in[2].gl_Position)
		),
		1.0
	);
}