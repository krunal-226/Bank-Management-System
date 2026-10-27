package com.bank3d.engine3d;

public class Vector3D {
    public double x, y, z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D copy() {
        return new Vector3D(x, y, z);
    }

    public Vector3D add(Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }

    public Vector3D subtract(Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    public Vector3D multiply(double s) {
        return new Vector3D(x * s, y * s, z * s);
    }

    public double dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vector3D cross(Vector3D v) {
        return new Vector3D(
            y * v.z - z * v.y,
            z * v.x - x * v.z,
            x * v.y - y * v.x
        );
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3D normalize() {
        double len = length();
        if (len < 1e-9) return new Vector3D(0, 0, 1);
        return new Vector3D(x / len, y / len, z / len);
    }

    public Vector3D rotateX(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Vector3D(x, y * c - z * s, y * s + z * c);
    }

    public Vector3D rotateY(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Vector3D(x * c + z * s, y, -x * s + z * c);
    }

    public Vector3D rotateZ(double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Vector3D(x * c - y * s, x * s + y * c, z);
    }

    public double[] project(double fov, double screenW, double screenH) {
        double dist = fov + z;
        if (dist < 10.0) dist = 10.0;
        double scale = fov / dist;
        double px = (x * scale) + (screenW / 2.0);
        double py = (y * scale) + (screenH / 2.0);
        return new double[]{ px, py, scale };
    }
}
